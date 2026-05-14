import { Page } from "@playwright/test";

export async function cleanupFollowState(page: Page, receiverId: number) {
    await page.request.delete(`http://127.0.0.1:8080/followRequest/unfollow/${receiverId}`, {
        failOnStatusCode: false,
    });

    await page.request.delete(`http://127.0.0.1:8080/followRequest/${receiverId}`, {
        failOnStatusCode: false,
    });
}