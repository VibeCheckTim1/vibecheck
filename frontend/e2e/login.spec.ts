import { test, expect, type Page } from "@playwright/test";

const SEEDED_USER = { username: "lsaric", password: "1234" };

async function fillLogin(page: Page, username: string, password: string) {
    await page.getByLabel("Username").fill(username);
    await page.getByLabel("Password").fill(password);
}

test.describe("Login", () => {
    test.beforeEach(async ({ page }) => {
        await page.goto("/");
    });

    test("logs in with seeded credentials and sets auth cookie", async ({ page, context }) => {
        await fillLogin(page, SEEDED_USER.username, SEEDED_USER.password);
        await page.getByRole("button", { name: "Login" }).click();

        await expect(page).toHaveURL(/\/home$/);

        const cookies = await context.cookies();
        expect(cookies.some((c) => c.name === "ACCESS")).toBe(true);
    });

    test("shows required-field errors when submitting empty form", async ({ page }) => {
        await page.getByRole("button", { name: "Login" }).click();

        await expect(page.getByText("This field is required").first()).toBeVisible();
        await expect(page).toHaveURL("/");
    });

    test("shows error toast on wrong credentials", async ({ page }) => {
        await fillLogin(page, SEEDED_USER.username, "definitely-not-the-password");
        await page.getByRole("button", { name: "Login" }).click();

        await expect(page.locator(".toast-message-container.error")).toBeVisible();
        await expect(page).toHaveURL("/");
    });

    test("Sign up link navigates to register page", async ({ page }) => {
        await page.getByRole("link", { name: "Sign up" }).click();
        await expect(page).toHaveURL(/\/register$/);
    });

    test("Google button initiates backend OAuth flow", async ({ page }) => {
        await page.route("**/oauth2/authorization/google", (route) => route.abort());

        const [request] = await Promise.all([
            page.waitForRequest("**/oauth2/authorization/google"),
            page.getByRole("button", { name: "Google" }).click(),
        ]);

        expect(request.url()).toBe("http://127.0.0.1:8080/oauth2/authorization/google");
    });

    test("Spotify button initiates backend OAuth flow", async ({ page }) => {
        await page.route("**/oauth2/authorization/spotify", (route) => route.abort());

        const [request] = await Promise.all([
            page.waitForRequest("**/oauth2/authorization/spotify"),
            page.getByRole("button", { name: "Spotify" }).click(),
        ]);

        expect(request.url()).toBe("http://127.0.0.1:8080/oauth2/authorization/spotify");
    });
});
