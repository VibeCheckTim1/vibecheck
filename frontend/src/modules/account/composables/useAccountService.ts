import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {User, type UserApi} from "../../../entities/user.ts";

export interface UpdateProfileRequest {
	"firstName": string,
	"lastName": string,
	"username": string,
	"bio": string,
	"isPrivate": boolean
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

export function useAccountService() {
	const {httpPatch, httpPost, httpDelete} = useHttpClient();

	async function updateAction(body: UpdateProfileRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/account`, body);
		return new User(data);
	}

	async function uploadAvatarAction(body: FormData): Promise<User> {
		const data = await httpPost<UserApi>(`/account/avatar`, body);
		return new User(data);
	}

	async function deleteAction(): Promise<void> {
		await httpDelete<void>(`/account`);
	}

	async function changePasswordAction(body: ChangePasswordRequest): Promise<void> {
		await httpPatch<void>(`/account/password`, body);
	}

	async function changeEmailAction(body: ChangeEmailRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/account/email`, body);
		return new User(data);
	}

	async function emailVerificationCodeAction(body: NewEmailRequest): Promise<void> {
		await httpPost<UserApi>(`/account/emailVerificationCode`, body);
	}

	async function confirmMailEdit(body: VerificationCodeRequest): Promise<User> {
		const data = await httpPatch<UserApi>(`/account/confirmMailEdit`, body);
		return new User(data);
	}

	return {
		updateAction,
		uploadAvatarAction,
		deleteAction,
		changePasswordAction,
		changeEmailAction,
		emailVerificationCodeAction,
		confirmMailEdit
	};
}
