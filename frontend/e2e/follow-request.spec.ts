import { test } from "@playwright/test";
import { ProfilePage } from "./pages/ProfilePage";
import { login, switchUser } from "./helpers/auth";
import { NotificationsPage } from "./pages/NotificationsPage";
import { cleanupFollowState } from "./helpers/cleanup";

const PRIVATE_RECEIVER = {
    id: 1,
    username: "lsaric",
    password: "1234",
};

const SENDER = {
    id: 2,
    username: "mgradiscaj",
    password: "1234",
};

const PUBLIC_RECEIVER = {
    id: 3,
    username: "kstjepanovic",
    password: "1234",
};


test.describe.serial("Follow request E2E", () => {
    test("private profile - clicking Follow changes status to Pending", async ({ page }) => {
        const profilePage = new ProfilePage(page);

        await login(page, SENDER.username, SENDER.password);

        await cleanupFollowState(page, PRIVATE_RECEIVER.id);

        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.expectFollowButtonText("Follow");

        await profilePage.clickFollow();

        await profilePage.expectFollowButtonText("Pending");

    });


    test("public profile - clicking Follow changes status immediately to Following", async ({ page }) => {
        const profilePage = new ProfilePage(page);

        await login(page, SENDER.username, SENDER.password);
        await cleanupFollowState(page, PUBLIC_RECEIVER.id);

        await profilePage.open(PUBLIC_RECEIVER.id);

        await profilePage.clickFollow();

        await profilePage.expectFollowButtonText("Following");

    });


    test("receiver sees follow request in notifications", async ({ page, context }) => {
        const profilePage = new ProfilePage(page);
        const notificationPage = new NotificationsPage(page);

        await switchUser(page, context, SENDER.username, SENDER.password);
        await cleanupFollowState(page, PRIVATE_RECEIVER.id);


        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.clickFollow();
        await profilePage.expectFollowButtonText("Pending");

        await switchUser(page, context, PRIVATE_RECEIVER.username, PRIVATE_RECEIVER.password);

        await notificationPage.open();
        await notificationPage.expectRequestVisible(SENDER.id);

    });


    test("receiver accepts follow request - sender status becomes Following", async ({ page, context }) => {
        const profilePage = new ProfilePage(page);
        const notificationPage = new NotificationsPage(page);

        await switchUser(page, context, SENDER.username, SENDER.password);
        await cleanupFollowState(page, PRIVATE_RECEIVER.id);

        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.clickFollow();
        await profilePage.expectFollowButtonText("Pending");

        await switchUser(page, context, PRIVATE_RECEIVER.username, PRIVATE_RECEIVER.password);

        await notificationPage.open();
        await notificationPage.expectRequestVisible(SENDER.id);
        await notificationPage.acceptRequest(SENDER.id);
        await notificationPage.expectRequestNotVisible(SENDER.id);

        await switchUser(page, context, SENDER.username, SENDER.password);

        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.expectFollowButtonText("Following");

    });


     test("receiver declines follow request - sender status becomes Follow", async ({ page, context }) => {
        const profilePage = new ProfilePage(page);
        const notificationPage = new NotificationsPage(page);

        await switchUser(page, context, SENDER.username, SENDER.password);
        await cleanupFollowState(page, PRIVATE_RECEIVER.id);

        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.clickFollow();
        await profilePage.expectFollowButtonText("Pending");

        await switchUser(page, context, PRIVATE_RECEIVER.username, PRIVATE_RECEIVER.password);

        await notificationPage.open();
        await notificationPage.expectRequestVisible(SENDER.id);
        await notificationPage.declineRequest(SENDER.id);
        await notificationPage.expectRequestNotVisible(SENDER.id);

        await switchUser(page, context, SENDER.username, SENDER.password);

        await profilePage.open(PRIVATE_RECEIVER.id);
        await profilePage.expectFollowButtonText("Follow");

    });



})






