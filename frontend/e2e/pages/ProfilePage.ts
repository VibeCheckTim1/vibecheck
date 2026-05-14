import { expect, type Page } from "@playwright/test";

export class ProfilePage {
    constructor(private page: Page) { }


    async open(userId: number) {
        await this.page.goto(`/account/${userId}`);
        await expect(this.page.getByTestId("profile-username")).toBeVisible();
    }

    async clickFollow() {
        await this.page.getByTestId("follow-button").click();
    }

    async expectFollowButtonText(text: "Follow" | "Pending" | "Following") {
        await expect(this.page.getByTestId("follow-button")).toHaveText(new RegExp(`^${text}$`, "i"));
    }

}