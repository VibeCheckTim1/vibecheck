import {ref} from "vue";

export interface Toast {
	id: number;
	message: string;
	type: "success" | "error" | "info";
	duration: number;
}

const toasts = ref<Toast[]>([]);
let nextId = 1;

export function useToast() {
	const DEFAULT_DURATION = 10000;

	function showToast(message: string, type: Toast["type"], duration = DEFAULT_DURATION) {
		const id = nextId++;
		const toast: Toast = {id, message, type, duration};
		toasts.value.push(toast);

		setTimeout(() => {
			removeToast(id);
		}, duration);
	}

	function removeToast(id: number) {
		toasts.value = toasts.value.filter(t => t.id !== id);
	}

	function clearAll() {
		toasts.value = [];
	}

	function showSuccess(message: string, duration = DEFAULT_DURATION) {
		showToast(message, "success", duration);
	}

	function showError(message: string, duration = DEFAULT_DURATION) {
		showToast(message, "error", duration);
	}

	function showInfo(message: string, duration = DEFAULT_DURATION) {
		showToast(message, "info", duration);
	}

	return {
		toasts,
		showToast,
		showSuccess,
		showError,
		showInfo,
		removeToast,
		clearAll,
	};
}
