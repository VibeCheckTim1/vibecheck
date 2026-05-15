import { expect, test } from "@playwright/test";
import { login } from "./helpers/auth";

const USER = {
    username: "lsaric",
    password: "1234",
};

test.describe("Account settings", () => {
    test.beforeEach(async ({ page }) => {
        await login(page, USER.username, USER.password);
        await page.goto("/account/update");
        await expect(page.getByText("Edit profile")).toBeVisible();
    });

    test("UpdateProfileView shows required validation on save", async ({ page }) => {
        await page.getByLabel("First name").fill("");
        await page.getByLabel("Last name").fill("");

        await page.getByRole("button", { name: "Save" }).first().click();

        await expect(page.getByText("This field is required").first()).toBeVisible();
        await expect(page).toHaveURL(/\/account\/update$/);
    });

    test("ChangeEmailForm validates email format", async ({ page }) => {
        const emailSection = page
            .locator(".form-section-container")
            .filter({ has: page.getByText("Email address") });

        await emailSection.getByRole("button", { name: "Change" }).click();

        await expect(page.getByRole("heading", { name: "Change email" })).toBeVisible();
        await page.getByLabel("New Email").fill("not-an-email");
        await page.getByRole("button", { name: "Send verification code" }).click();

        await expect(page.getByText("Email address is not in a valid format")).toBeVisible();
    });

    test("ChangeEmailForm shows required validation when new email is cleared", async ({ page }) => {
        const emailSection = page
            .locator(".form-section-container")
            .filter({ has: page.getByText("Email address") });

        await emailSection.getByRole("button", { name: "Change" }).click();
        await expect(page.getByRole("heading", { name: "Change email" })).toBeVisible();

        const newEmailInput = page.getByLabel("New Email");
        await newEmailInput.fill("temp@example.com");
        await newEmailInput.fill("");
        await newEmailInput.blur();

        await expect(page.locator(".form-control-errors li").filter({ hasText: "This field is required" })).toBeVisible();
    });

    test("ChangePasswordForm shows mismatch error", async ({ page }) => {
        const passwordSection = page
            .locator(".form-section-container")
            .filter({ has: page.getByText("Password") });

        await passwordSection.getByRole("button", { name: "Change" }).click();

        await expect(page.getByRole("heading", { name: "Change password" })).toBeVisible();
        await page.getByLabel("Current password").fill("1234");
        await page.getByLabel("New password", { exact: true }).fill("new-pass-1234");
        await page.getByLabel("Confirm new password").fill("different-pass-1234");

        await expect(page.getByText("Passwords do not match.")).toBeVisible();
    });

    test("ChangePasswordForm shows required validations on empty inputs", async ({ page }) => {
        const passwordSection = page
            .locator(".form-section-container")
            .filter({ has: page.getByText("Password") });

        await passwordSection.getByRole("button", { name: "Change" }).click();
        await expect(page.getByRole("heading", { name: "Change password" })).toBeVisible();

        await page.getByRole("button", { name: "Save" }).nth(1).click();

        await expect(page.locator(".form-control-errors li").filter({ hasText: "This field is required" })).toHaveCount(3);
    });

    test("UpdateProfileView shows success toast after successful save", async ({ page }) => {
        await page.getByRole("button", { name: "Save" }).first().click();

        await expect(page.locator(".toast-message-container.success")).toBeVisible();
        await expect(page.getByText("Updated successfully!")).toBeVisible();
    });

    test("ChangePasswordForm shows error toast when API returns error", async ({ page }) => {
        await page.route("**/account/password", async (route) => {
            await route.fulfill({
                status: 400,
                contentType: "application/json",
                body: JSON.stringify({ message: "Current password is incorrect." }),
            });
        });

        const passwordSection = page
            .locator(".form-section-container")
            .filter({ has: page.getByText("Password") });

        await passwordSection.getByRole("button", { name: "Change" }).click();
        await expect(page.getByRole("heading", { name: "Change password" })).toBeVisible();

        await page.getByLabel("Current password").fill("wrong-password");
        await page.getByLabel("New password", { exact: true }).fill("new-pass-1234");
        await page.getByLabel("Confirm new password").fill("new-pass-1234");
        await page.getByRole("button", { name: "Save" }).nth(1).click();

        await expect(page.locator(".toast-message-container.error")).toBeVisible();
        await expect(page.getByText("Current password is incorrect.")).toBeVisible();
    });

    test("UploadAvatarForm validates required file and format", async ({ page }) => {
        await page.locator(".change-avatar-button").click();

        await expect(page.getByRole("heading", { name: "Upload profile picture" })).toBeVisible();

        await page.getByRole("button", { name: "Save" }).nth(1).click();
        await expect(page.getByText("File is required")).toBeVisible();

        await page
            .locator('input[type="file"]')
            .setInputFiles({
                name: "avatar.txt",
                mimeType: "text/plain",
                buffer: Buffer.from("not-an-image"),
            });

        await expect(
            page.locator(".form-control-errors li").filter({
                hasText: "Supported formats are jpg, jpeg, png, svg",
            }),
        ).toBeVisible();
    });
});
