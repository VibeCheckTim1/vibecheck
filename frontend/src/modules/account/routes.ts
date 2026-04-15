import type {RouteRecordRaw} from "vue-router";
import ProfileView from "./views/ProfileView.vue";
import UpdateProfileView from "./views/UpdateProfileView.vue";
import EmptyLayout from "../../components/EmptyLayout.vue";
import ConfirmEmailChangeView from "./views/ConfirmEmailChangeView.vue";

export const accountRoutes: RouteRecordRaw[] = [
	{
		path: "/account",
		component: EmptyLayout,
		children: [
			{
				path: ":userId",
				name: "account",
				component: ProfileView,
			},
			{
				path: "update",
				name: "updateProfile",
				component: UpdateProfileView,
			},
		]
		
	},


	{
		path: "/confirmEmailChange",
		name: "confirmEmailChange",
		component: ConfirmEmailChangeView,
	},

];
