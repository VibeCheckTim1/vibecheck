import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {User, type UserApi} from "../../../entities/user.ts";

export interface LoginRequest {
	username: string;
	password: string;
}

export interface RegisterRequest {
	firstName: string;
	lastName: string;
	username: string;
	bio: string;
	email: string;
	password: string;
}

export function useLoginService() {
	const {httpPost} = useHttpClient();

	async function loginAction(body: LoginRequest): Promise<User> {
		const data = await httpPost<UserApi>("/security/login", body);
		return new User(data);
	}

	async function registerAction(body: RegisterRequest): Promise<User> {
		const data = await httpPost<UserApi>("/security/register", body);
		return new User(data);
	}

	return {
		loginAction,
		registerAction
	};
}
