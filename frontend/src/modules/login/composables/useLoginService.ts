import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {User, type UserApi} from "../../../entities/user.ts";

export interface LoginRequest {
	email: string;
	password: string;
}

export function useLoginService() {
	const {httpPost} = useHttpClient();

	async function loginAction(body: LoginRequest): Promise<User> {
		const data = await httpPost<UserApi>("/auth/login", body);
		return new User(data);
	}

	return {
		loginAction,
	};
}
