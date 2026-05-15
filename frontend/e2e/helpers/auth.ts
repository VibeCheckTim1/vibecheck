import { expect, type BrowserContext, type Page } from "@playwright/test";

export async function login(page: Page, username: string, password: string) {
    await page.goto("/");
    await page.getByLabel("Username").fill(username);
    await page.getByLabel("Password").fill(password);
    await page.getByRole("button", { name: "Login" }).click();

    await expect(page).toHaveURL(/\home$/);
}

export async function switchUser(
        page: Page,
        context: BrowserContext,
        username: string,
        password: string
    ) {
        await context.clearCookies();
        await login(page, username, password);
    }