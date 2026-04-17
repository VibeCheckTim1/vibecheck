import {defineStore} from "pinia";
import {computed, ref, type Ref} from "vue";
import type {FollowRequestResponse} from "../composables/useFollowingService.ts";

export const useFollowingStore = defineStore("followingStore", () => {
	const _followRequests = ref<FollowRequestResponse[]>([]);

	function setFollowRequests(newFollows: FollowRequestResponse[]): void {
		_followRequests.value = newFollows;
	}

	const followRequests = computed(() => {
		return [..._followRequests.value].sort((a, b) => {
			return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
		});
	});

	return {
		followRequests: followRequests as Ref<FollowRequestResponse[]>,
		setFollowRequests,
	};
});
