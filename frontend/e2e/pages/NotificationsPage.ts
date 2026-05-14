import { expect, Page } from "@playwright/test";

export class NotificationsPage {
    constructor(private page: Page) { }


    async open() {
        await this.page.goto("/notifications");
    }

    async expectRequestVisible(senderId: number) {
        await expect(this.page.getByTestId(`follow-request-item-${senderId}`)).toBeVisible();
    }

    async expectRequestNotVisible(senderId: number) {
        await expect(this.page.getByTestId(`follow-request-item-${senderId}`)).not.toBeVisible();
    }

    async acceptRequest(senderId: number) {
        await this.page.getByTestId(`accept-follow-request-button-${senderId}`).click();
    }

    async declineRequest(senderId: number) {
        await this.page.getByTestId(`decline-follow-request-button-${senderId}`).click();
    }


}