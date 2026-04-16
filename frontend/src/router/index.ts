import {createRouter, createWebHistory} from "vue-router";
import {loginRoutes} from "../modules/login/routes.ts";
import {homeRoutes} from "../modules/home/routes.ts";
import {searchRoutes} from "../modules/search/routes.ts";
import {createRoutes} from "../modules/create/routes.ts";
import {notificationsRoutes} from "../modules/notifications/routes.ts";
import {accountRoutes} from "../modules/account/routes.ts";
import {useState} from "../composables/useState.ts";
import {useHttpClient} from "../composables/useHttpClient.ts";
import {User, type UserApi} from "../entities/user.ts";
import {useDialog} from "../composables/useDialog.ts";
import {useConfirm} from "../composables/useConfirm.ts";

let previousRouteName: string | null = null;
const router = createRouter({
	history: createWebHistory(),
	routes: [
		...loginRoutes,
		...homeRoutes,
		...searchRoutes,
		...createRoutes,
		...notificationsRoutes,
		...accountRoutes
	],
});

router.beforeEach(async (_to, from) => {
	previousRouteName = from.name as string | null;
	const {closeAllDialogs} = useDialog();
	const {closeConfirm} = useConfirm();
	closeAllDialogs();
	closeConfirm();

	if (_to.name === "login" || _to.name === "register") {
		return true;
	}

	const {httpGet} = useHttpClient();
	const {currentUser, setUser} = useState();

	if (!currentUser.value) {
		try {
			const data = await httpGet<UserApi>("/security/current-user");
			setUser(new User(data));
		}
		catch {
			return {name: "login"};
		}
	}

	return true;
});
export default router;

export function getPreviousRouteName() {
	return previousRouteName;
}
