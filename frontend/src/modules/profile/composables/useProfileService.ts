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
	const {httpPut} = useHttpClient();

	async function updateAction(body: UpdateProfileRequest): Promise<User> {
		const data = await httpPut<UserApi>(`/user/edit/${String(userId)}`, body);
		return new User(data);
	}

	return {
		updateAction,
	};
}
