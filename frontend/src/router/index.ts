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

router.beforeEach(async () => {
	const {httpGet} = useHttpClient();
	const {
		currentUser,
		setUser,
	} = useState();

	/*
	 * USER
	 */
	if (!currentUser.value) {
		try {
			const data = await httpGet<UserApi>("/state/user-state");
			setUser(new User(data));
		}
		catch {
			return;
		}
	}

	return true;
});

export default router;
