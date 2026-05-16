import { expect, test } from "@playwright/test";
import { login } from "./helpers/auth";

const USER = {
    username: "lsaric",
    password: "1234",
};

test.describe("Search module", () => {
    test.beforeEach(async ({ page }) => {
        await login(page, USER.username, USER.password);
        await page.goto("/search");
        await expect(page).toHaveURL(/\/search$/);
    });

    test("renders search page static sections", async ({ page }) => {
        await expect(page.getByText("Trending now")).toBeVisible();
        await expect(page.getByText("Browse by Genre")).toBeVisible();
        await expect(page.getByText("Hip Hop")).toBeVisible();
        await expect(page.getByText("Billie Eilish")).toBeVisible();
    });

    test("shows grouped search results for query with at least 2 characters", async ({ page }) => {
        await page.route("**/search*", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify([
                    { id: 1, type: "user", titleText: "lsaric", subtitleText: "Luka Saric" },
                    { id: 9, type: "artist", titleText: "Lorde", subtitleText: "18.2M listeners" },
                ]),
            });
        });

        await page.getByPlaceholder("Search...").fill("lo");

        await expect(page.getByText("Users")).toBeVisible();
        await expect(page.getByText("Artists")).toBeVisible();
        await expect(page.getByText("lsaric")).toBeVisible();
        await expect(page.getByText("Lorde")).toBeVisible();
    });

    test("does not call API and keeps dropdown hidden for one character query", async ({ page }) => {
        let wasCalled = false;
        await page.route("**/search*", async (route) => {
            wasCalled = true;
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify([]),
            });
        });

        await page.getByPlaceholder("Search...").fill("a");
        await page.waitForTimeout(450);

        expect(wasCalled).toBe(false);
        await expect(page.locator(".search-dropdown")).toHaveCount(0);
    });

    test("clicking user result navigates to account page", async ({ page }) => {
        await page.route("**/search*", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify([
                    { id: 3, type: "user", titleText: "kstjepanovic", subtitleText: "Karlo Stjepanovic" },
                ]),
            });
        });

        await page.getByPlaceholder("Search...").fill("ks");
        await expect(page.getByText("kstjepanovic")).toBeVisible();

        await page.getByText("kstjepanovic").click();
        await expect(page).toHaveURL(/\/account\/3$/);
    });
});
