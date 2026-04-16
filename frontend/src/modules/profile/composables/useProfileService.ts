import { useHttpClient } from "../../../composables/useHttpClient.ts";
import { User, type UserApi } from "../../../entities/user.ts";

export interface UpdateProfileRequest {
	"firstName": string,
	"lastName": string,
	"username": string,
	"bio": string,
	"visibility": string
}

export interface ChangePasswordRequest {
	"oldPassword": string,
	"newPassword": string
}

export interface ChangeEmailRequest {
	"newEmail": string
}

export interface NewEmailRequest {
	"newEmail": string
}

export interface VerificationCodeRequest {
	"code": string
}

export function useProfileService(userId: number) {
	const { httpGet, httpPatch, httpPost, httpDelete } = useHttpClient();

	async function getUser(): Promise<User> {
		const data = await httpGet<UserApi>(`/user/${String(userId)}`);
		return new User(data);
	}

	async function logoutAction(): Promise<void> {
		await httpPost<void>(`/auth/logout`, {});
	}

	async function updateAction(body: UpdateProfileRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/user/edit/${String(userId)}`, body);
		return new User(data);
	}

	async function uploadAvatarAction(body: FormData): Promise<User> {
		const data = await httpPost<UserApi>(`/user/addAvatar`, body);
		return new User(data);
	}

	async function deleteAction(): Promise<void> {
		await httpDelete<void>(`/user/delete/${String(userId)}`);
	}

	async function changePasswordAction(body: ChangePasswordRequest): Promise<void> {
		await httpPatch<void>(`/user/changePassword`, body);
	}

	async function changeEmailAction(body: ChangeEmailRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/user/changeEmail`, body);
		return new User(data);
	}

	async function emailVerificationCodeAction(body: NewEmailRequest): Promise<void> {
		await httpPost<UserApi>(`/user/emailVerificationCode`, body);
	}

	async function confirmMailEdit(body: VerificationCodeRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/user/confirmMailEdit`, body);
		return new User(data);
	}


	return {
		getUser,
		logoutAction,
		updateAction,
		uploadAvatarAction,
		deleteAction,
		changePasswordAction,
		changeEmailAction,
		emailVerificationCodeAction,
		confirmMailEdit
	};
}
