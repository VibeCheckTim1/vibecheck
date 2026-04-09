import {createRouter, createWebHistory} from "vue-router";
import {loginRoutes} from "../modules/login/routes.ts";
import {homeRoutes} from "../modules/home/routes.ts";
import {searchRoutes} from "../modules/search/routes.ts";
import {createRoutes} from "../modules/create/routes.ts";
import {notificationsRoutes} from "../modules/notifications/routes.ts";
import {profileRoutes} from "../modules/profile/routes.ts";
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
		...profileRoutes
	],
});

router.beforeEach(async (to, from) => {
	previousRouteName = from.name as string | null;
	const guestRoutes = ["login", "register"];
	const {closeAllDialogs} = useDialog();
	const {closeConfirm} = useConfirm();
	closeAllDialogs();
	closeConfirm();

	const isAuthenticated = !!localStorage.getItem("authenticated");
	const isGuestRoute = guestRoutes.includes(to.name as string);

	if (!isAuthenticated && !isGuestRoute) {
		return {name: "login"};
	}

	if (isAuthenticated && isGuestRoute) {
		return {name: "home"};
	}

	if (isAuthenticated) {
		const {httpGet} = useHttpClient();
		const {currentUser, setUser} = useState();

		if (!currentUser.value) {
			try {
				const data = await httpGet<UserApi>("/state/user-state");
				setUser(new User(data));
			}
			catch {
				localStorage.removeItem("authenticated");
				return {name: "login"};
			}
		}
	}

	return true;
});
export default router;

export function getPreviousRouteName() {
	return previousRouteName;
}
