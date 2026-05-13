import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
    testDir: "./e2e",
    timeout: 30_000,
    expect: { timeout: 5_000 },
    fullyParallel: true,
    reporter: [["list"], ["html", { open: "never" }]],
    use: {
        baseURL: "http://127.0.0.1:5173",
        trace: "on-first-retry",
        screenshot: "only-on-failure",
    },
    projects: [
        { name: "chromium", use: { ...devices["Desktop Chrome"] } },
        { name: "firefox", use: { ...devices["Desktop Firefox"] } },
        { name: "webkit", use: { ...devices["Desktop Safari"] } },
    ],
    webServer: [
        {
            command: `${process.platform === "win32" ? "mvnw.cmd" : "./mvnw"} spring-boot:run -Dspring-boot.run.profiles=dev`,
            cwd: "../vibecheck",
            port: 8080,
            reuseExistingServer: true,
            stdout: "pipe",
            stderr: "pipe",
            timeout: 180_000,
        },
        {
            command: "npm run dev",
            url: "http://127.0.0.1:5173",
            reuseExistingServer: true,
            stdout: "pipe",
            stderr: "pipe",
            timeout: 60_000,
        },
    ],
});
