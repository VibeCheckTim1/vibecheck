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
	const {httpPut, httpPost} = useHttpClient();

	async function updateAction(body: UpdateProfileRequest): Promise<User> {
		const data = await httpPut<UserApi>(`/user/edit/${String(userId)}`, body);
		return new User(data);
	}

	async function uploadAvatar(body: FormData): Promise<User> {
		const data = await httpPost<UserApi>(`/user/addAvatar`, body);
		return new User(data);
	}

	return {
		updateAction,
		uploadAvatar
	};
}
