import type {RouteRecordRaw} from "vue-router";
import ProfileView from "./views/ProfileView.vue";
import UpdateProfileView from "./views/UpdateProfileView.vue";
import EmptyLayout from "../../components/EmptyLayout.vue";
import ConfirmEmailChangeView from "./views/ConfirmEmailChangeView.vue";

export const profileRoutes: RouteRecordRaw[] = [
	{
		path: "/profile",
		component: EmptyLayout,
		children: [
			{
				path: "",
				name: "profile",
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
