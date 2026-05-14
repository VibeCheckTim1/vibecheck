import { test, expect } from "@playwright/test";

function uniqueId() {
    return Math.random().toString(36).slice(2, 10);
}

test.describe("Register", () => {
    test.beforeEach(async ({ page }) => {
        await page.goto("/register");
    });

    test("registers a new user and lands on home with welcome toast", async ({ page, context }) => {
        const id = uniqueId();
        const username = `pw_${id}`;
        const email = `pw_${id}@example.com`;

        await page.getByLabel("First name").fill("Play");
        await page.getByLabel("Last name").fill("Wright");
        await page.getByLabel("Username").fill(username);
        await page.getByLabel("Email").fill(email);
        await page.getByLabel("Password", { exact: true }).fill("test1234");
        await page.getByLabel("Repeat password").fill("test1234");

        await page.getByRole("button", { name: "Register" }).click();

        await expect(page).toHaveURL(/\/home$/);
        await expect(page.getByText("Welcome to VibeCheck!")).toBeVisible();

        const cookies = await context.cookies();
        expect(cookies.some((c) => c.name === "ACCESS")).toBe(true);
    });

    test("shows error toast when username is already taken", async ({ page }) => {
        await page.getByLabel("First name").fill("Play");
        await page.getByLabel("Last name").fill("Wright");
        await page.getByLabel("Username").fill("lsaric");
        await page.getByLabel("Email").fill(`pw_${uniqueId()}@example.com`);
        await page.getByLabel("Password", { exact: true }).fill("test1234");
        await page.getByLabel("Repeat password").fill("test1234");

        await page.getByRole("button", { name: "Register" }).click();

        await expect(page.locator(".toast-message-container.error")).toBeVisible();
        await expect(page).toHaveURL(/\/register$/);
    });

    test("shows mismatch toast when passwords do not match", async ({ page }) => {
        const id = uniqueId();
        await page.getByLabel("First name").fill("Play");
        await page.getByLabel("Last name").fill("Wright");
        await page.getByLabel("Username").fill(`pw_${id}`);
        await page.getByLabel("Email").fill(`pw_${id}@example.com`);
        await page.getByLabel("Password", { exact: true }).fill("test1234");
        await page.getByLabel("Repeat password").fill("different1234");

        await page.getByRole("button", { name: "Register" }).click();

        await expect(page.getByText("Passwords don't match!")).toBeVisible();
        await expect(page).toHaveURL(/\/register$/);
    });

    test("shows email validation error for malformed email", async ({ page }) => {
        await page.getByLabel("First name").fill("Play");
        await page.getByLabel("Last name").fill("Wright");
        await page.getByLabel("Username").fill(`pw_${uniqueId()}`);
        await page.getByLabel("Email").fill("not-an-email");
        await page.getByLabel("Password", { exact: true }).fill("test1234");
        await page.getByLabel("Repeat password").fill("test1234");

        await page.getByRole("button", { name: "Register" }).click();

        await expect(page.getByText("Email address is not in a valid format")).toBeVisible();
        await expect(page).toHaveURL(/\/register$/);
    });

    test("shows minLength error for short password", async ({ page }) => {
        await page.getByLabel("First name").fill("Play");
        await page.getByLabel("Last name").fill("Wright");
        await page.getByLabel("Username").fill(`pw_${uniqueId()}`);
        await page.getByLabel("Email").fill(`pw_${uniqueId()}@example.com`);
        await page.getByLabel("Password", { exact: true }).fill("ab");
        await page.getByLabel("Repeat password").fill("ab");

        await page.getByRole("button", { name: "Register" }).click();

        await expect(page.getByText("Field must contain at least 4 characters")).toBeVisible();
        await expect(page).toHaveURL(/\/register$/);
    });

    test("Sign in link navigates to login page", async ({ page }) => {
        await page.getByRole("link", { name: "Sign in" }).click();
        await expect(page).toHaveURL("/");
    });
});
