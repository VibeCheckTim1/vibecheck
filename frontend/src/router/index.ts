import {createRouter, createWebHistory} from "vue-router";
import {loginRoutes} from "../modules/login/routes.ts";

const router = createRouter({
	history: createWebHistory(),
	routes: [
		...loginRoutes
	],
});

export default router;
