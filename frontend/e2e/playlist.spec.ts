import {expect, test, type Page} from "@playwright/test";
import {login} from "./helpers/auth";

const USER = {
    username: "lsaric",
    password: "1234",
};

const PLAYLIST_ID = 4201;

function buildPlaylist(userId: number) {
    return {
        id: PLAYLIST_ID,
        name: "Evening Vibes",
        isFavorite: true,
        isPublic: true,
        songCount: 12,
        userId,
        username: USER.username,
    };
}

async function getCurrentUserId(page: Page): Promise<number> {
    const profileHref = await page.getByRole("link", {name: "Profile"}).getAttribute("href");
    const matchedUserId = profileHref?.match(/\/account\/(\d+)$/)?.[1];
    if (!matchedUserId) {
        throw new Error("Could not resolve current user id from profile link.");
    }

    return Number(matchedUserId);
}

test.describe("Playlist module", () => {
    test.beforeEach(async ({page}) => {
        await login(page, USER.username, USER.password);
    });

    test("renders playlists on profile and opens playlist details", async ({page}) => {
        const currentUserId = await getCurrentUserId(page);
        const playlist = buildPlaylist(currentUserId);

        await page.route("**/users/*/playlists", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify([
                    playlist,
                    {
                        ...playlist,
                        id: 4202,
                        name: "Late Night Mix",
                        songCount: 5,
                    },
                ]),
            });
        });

        await page.route("http://127.0.0.1:8080/playlists/4201", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify(playlist),
            });
        });

        await page.getByRole("link", {name: "Profile"}).click();

        await expect(page.getByText("My playlists")).toBeVisible();
        await expect(page.getByText("Evening Vibes")).toBeVisible();
        await expect(page.getByText("Late Night Mix")).toBeVisible();

        await page.getByRole("link", {name: /Evening Vibes/i}).click();
        await expect(page).toHaveURL(/\/playlists\/4201$/);
        await expect(page.getByRole("heading", {name: "Evening Vibes"})).toBeVisible();
    });

    test("creates a playlist from create page", async ({page}) => {
        const currentUserId = await getCurrentUserId(page);
        const playlist = buildPlaylist(currentUserId);

        await page.route("http://127.0.0.1:8080/playlists", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify({
                    ...playlist,
                    id: 4301,
                    name: "Roadtrip",
                    isFavorite: false,
                }),
            });
        });

        await page.goto("/create");
        await page.getByLabel("Playlist name").fill("Roadtrip");
        await page.getByRole("button", {name: "Create playlist"}).click();

        await expect(page.locator(".toast-message-container.success")).toBeVisible();
        await expect(page.getByText("Playlist created successfully!")).toBeVisible();
        await expect(page).toHaveURL(/\/account\/\d+$/);
    });

    test("shows playlist details", async ({page}) => {
        const currentUserId = await getCurrentUserId(page);
        const playlist = buildPlaylist(currentUserId);

        await page.route("http://127.0.0.1:8080/playlists/4201", async (route) => {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify(playlist),
            });
        });

        await page.goto("/playlists/4201");

        await expect(page.getByText("Playlist details")).toBeVisible();
        await expect(page.getByRole("heading", {name: "Evening Vibes"})).toBeVisible();
        await expect(page.getByText("@lsaric")).toBeVisible();
        await expect(page.getByText("Public")).toBeVisible();
        await expect(page.getByText("Yes")).toBeVisible();
        await expect(page.getByText("12")).toBeVisible();
    });

    test("edits playlist from details page", async ({page}) => {
        const currentUserId = await getCurrentUserId(page);
        const playlist = buildPlaylist(currentUserId);

        await page.route("http://127.0.0.1:8080/playlists/4201", async (route) => {
            if (route.request().method() === "GET") {
                await route.fulfill({
                    status: 200,
                    contentType: "application/json",
                    body: JSON.stringify(playlist),
                });
                return;
            }

            if (route.request().method() === "PATCH") {
                await route.fulfill({
                    status: 200,
                    contentType: "application/json",
                    body: JSON.stringify({
                        ...playlist,
                        name: "Updated Vibes",
                        isPublic: false,
                    }),
                });
                return;
            }

            await route.fallback();
        });

        await page.goto("/playlists/4201");
        await page.getByRole("button", {name: "Edit"}).click();

        await expect(page.getByRole("heading", {name: "Edit playlist"})).toBeVisible();
        await page.getByLabel("Playlist name").fill("Updated Vibes");
        await page.locator('input[type="checkbox"]').nth(1).click();
        await page.getByRole("button", {name: "Save"}).click();

        await expect(page.locator(".toast-message-container.success")).toBeVisible();
        await expect(page.getByText("Playlist updated successfully!")).toBeVisible();
        await expect(page.getByRole("heading", {name: "Updated Vibes"})).toBeVisible();
        await expect(page.getByText("Private")).toBeVisible();
    });

    test("deletes playlist from details page", async ({page}) => {
        const currentUserId = await getCurrentUserId(page);
        const playlist = buildPlaylist(currentUserId);

        await page.route("http://127.0.0.1:8080/playlists/4201", async (route) => {
            if (route.request().method() === "GET") {
                await route.fulfill({
                    status: 200,
                    contentType: "application/json",
                    body: JSON.stringify(playlist),
                });
                return;
            }

            if (route.request().method() === "DELETE") {
                await route.fulfill({
                    status: 200,
                    contentType: "application/json",
                    body: "",
                });
                return;
            }

            await route.fallback();
        });

        await page.goto("/playlists/4201");
        await page.getByRole("button", {name: "Delete"}).click();
        await page.getByRole("button", {name: "Yes, delete playlist"}).click();

        await expect(page.locator(".toast-message-container.success")).toBeVisible();
        await expect(page.getByText("Playlist deleted successfully!")).toBeVisible();
        await expect(page).toHaveURL(new RegExp(`/account/${playlist.userId}$`));
    });
});
