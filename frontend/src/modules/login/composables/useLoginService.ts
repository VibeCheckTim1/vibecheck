import {useHttpClient} from "../../../composables/useHttpClient.ts";

export interface LoginRequest {
	email: string;
	password: string;
}

export function useLoginService() {
	const {httpPost} = useHttpClient();

	async function loginAction(body: LoginRequest): Promise<void> {
		await httpPost<void>("/auth/login", body);
	}

	return {
		loginAction,
	};
}
