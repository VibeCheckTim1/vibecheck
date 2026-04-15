import {useHttpClient} from "./useHttpClient.ts";

export function useSecurityService() {
	const {httpPost} = useHttpClient();

	async function logoutAction(): Promise<void> {
		await httpPost<void>(`/security/logout`, {});
	}

	return {
		logoutAction,
	};
}
