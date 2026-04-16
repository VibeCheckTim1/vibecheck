import {useHttpClient} from "./useHttpClient.ts";
import {User, type UserApi} from "../entities/user.ts";

export function useUserService() {
	const {httpGet} = useHttpClient();

	async function getUser(userId: number): Promise<User> {
		const data = await httpGet<UserApi>(`/users/${String(userId)}`);
		return new User(data);
	}

	return {
		getUser,
	};
}
