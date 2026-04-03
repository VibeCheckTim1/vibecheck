import {computed, markRaw, ref} from "vue";

type DialogInstance = {
	id: string;
	component: any;
	title: string;
	props: Record<string, any>;
};

const _dialogs = ref<DialogInstance[]>([]);

function generateDialogId(): string {
	return crypto.randomUUID();
}

export function useDialog() {
	const dialogs = computed<DialogInstance[]>(() => _dialogs.value);

	function openDialog(
		componentToRender: any,
		dialogTitle?: string,
		componentProps: Record<string, any> = {}
	): string {
		const id = generateDialogId();

		_dialogs.value.push({
			id: id,
			component: markRaw(componentToRender),
			title: dialogTitle ?? "",
			props: componentProps,
		});

		return id;
	}

	function closeDialog(id?: string): void {
		if (id !== undefined) {
			_dialogs.value = _dialogs.value.filter(dialog => {
				return dialog.id !== id;
			});
			return;
		}

		_dialogs.value.pop();
	}

	function closeAllDialogs(): void {
		_dialogs.value = [];
	}

	return {
		dialogs,
		openDialog,
		closeDialog,
		closeAllDialogs,
	};
}
