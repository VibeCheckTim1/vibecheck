// Runs after the frontend production build (npm "postbuild" hook).
// Uses Playwright to call the backend health endpoint, then posts a Discord
// webhook — mirroring the backend's StartupNotifier.

import { request } from "@playwright/test";

const APP_NAME = "VibeCheck";
const webhookUrl = process.env.DISCORD_WEBHOOK_URL;

// Render sets RENDER=true in its build environment. On Render we hit the
// production backend; locally we hit the dev backend, so a local build never
// pings production by accident.
const onRender = !!process.env.RENDER;
const apiBaseUrl = onRender
  ? process.env.VITE_API_BASE_URL
  : "http://127.0.0.1:8080";

async function checkBackendHealth(context) {
  if (!apiBaseUrl) {
    return "⚠️ backend URL not set, skipped health check";
  }

  const healthUrl = `${apiBaseUrl}/health`;
  // Render's free tier spins services down when idle; the first request after
  // a cold start can take well over a minute, so retry before giving up.
  const attempts = onRender ? 2 : 1;
  const timeout = onRender ? 120_000 : 10_000;

  for (let attempt = 1; attempt <= attempts; attempt++) {
    try {
      const response = await context.get(healthUrl, { timeout });
      if (response.ok()) {
        const body = await response.json();
        return `backend health: \`${body.status}\``;
      }
      if (attempt === attempts) {
        return `⚠️ backend health check failed (HTTP ${response.status()})`;
      }
    } catch (e) {
      if (attempt === attempts) {
        return `⚠️ backend unreachable: ${e.message}`;
      }
    }
  }
}

async function main() {
  if (!webhookUrl) {
    console.log("DISCORD_WEBHOOK_URL not set, skipping deploy notification");
    return;
  }

  const context = await request.newContext();
  try {
    const healthLine = await checkBackendHealth(context);
    const headline = onRender
      ? `✅ ${APP_NAME} - frontend has been deployed`
      : `🔧 ${APP_NAME} - frontend built locally`;
    await context.post(webhookUrl, {
      data: { content: `${headline}\n${healthLine}` },
    });
    console.log("Sent Discord deploy notification");
  } finally {
    await context.dispose();
  }
}

main().catch((e) => {
  // Never fail the build because of a notification.
  console.warn("Failed to send Discord deploy notification:", e.message);
});
