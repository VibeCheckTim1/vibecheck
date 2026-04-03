import {createRouter, createWebHistory} from "vue-router";
import {loginRoutes} from "../modules/login/routes.ts";
import {homeRoutes} from "../modules/home/routes.ts";
import {searchRoutes} from "../modules/search/routes.ts";
import {createRoutes} from "../modules/create/routes.ts";
import {notificationsRoutes} from "../modules/notifications/routes.ts";
import {profileRoutes} from "../modules/profile/routes.ts";

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

export default router;
