import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {User, type UserApi} from "../../../entities/user.ts";

export interface UpdateProfileRequest {
	"firstName": string,
	"lastName": string,
	"username": string,
	"bio": string,
	"visibility": string
}

export function useProfileService(userId: number) {
	const {httpPut, httpPost, httpDelete} = useHttpClient();

	async function logoutAction(): Promise<void> {
		await httpPost<void>(`/auth/logout`, {});
	}

	async function updateAction(body: UpdateProfileRequest): Promise<User> {
		const data = await httpPut<UserApi>(`/user/edit/${String(userId)}`, body);
		return new User(data);
	}

	async function uploadAvatarAction(body: FormData): Promise<User> {
		const data = await httpPost<UserApi>(`/user/addAvatar`, body);
		return new User(data);
	}

	async function deleteAction(): Promise<void> {
		await httpDelete<void>(`/user/delete/${String(userId)}`);
	}

	return {
		logoutAction,
		updateAction,
		uploadAvatarAction,
		deleteAction
	};
}
