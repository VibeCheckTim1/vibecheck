import type {RouteRecordRaw} from "vue-router";
import NotificationsView from "./views/NotificationsView.vue";

export const notificationsRoutes: RouteRecordRaw[] = [
	{
		path: "/notifications",
		name: "notifications",
		component: NotificationsView,
	},
];
