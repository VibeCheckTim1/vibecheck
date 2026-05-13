import { test, expect } from "@playwright/test";

const SEEDED_USER = { username: "lsaric", password: "1234" };

test.describe("Logout", () => {
    test("logs in then logs out, clearing the auth cookie", async ({ page, context }) => {
        await page.goto("/");
        await page.getByLabel("Username").fill(SEEDED_USER.username);
        await page.getByLabel("Password").fill(SEEDED_USER.password);
        await page.getByRole("button", { name: "Login" }).click();
        await expect(page).toHaveURL(/\/home$/);

        await page.getByRole("link", { name: "Profile" }).click();
        await expect(page).toHaveURL(/\/account\/\d+$/);

        await page.getByRole("button", { name: "Logout" }).click();
        await expect(page).toHaveURL("/");

        const cookies = await context.cookies();
        expect(cookies.some((c) => c.name === "ACCESS" && c.value !== "")).toBe(false);
    });
});
