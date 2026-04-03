import {nextTick, shallowRef} from "vue";

const isVisible = shallowRef(false);
const title = shallowRef<string>("");
const subtitle = shallowRef<string>("");
const iconClass = shallowRef<string | null>(null);
const acceptMessage = shallowRef<string | null>(null);
const isDanger = shallowRef<boolean>(false);
let resolveFn: ((confirmed: boolean) => void) | null = null;

export function useConfirm() {
	async function openConfirm(options: {
		title: string;
		subtitle?: string;
		iconClass?: string;
		acceptMessage?: string;
		isDanger?: boolean;
	}): Promise<boolean> {
		closeConfirm();
		await nextTick();

		title.value = options.title;
		subtitle.value = options.subtitle ?? "";
		iconClass.value = options.iconClass ?? null;
		acceptMessage.value = options.acceptMessage ?? null;
		isDanger.value = options.isDanger ?? true;
		isVisible.value = true;

		return new Promise<boolean>((resolve) => {
			resolveFn = resolve;
		});
	}

	function accept() {
		isVisible.value = false;
		resolveFn?.(true);
		resolveFn = null;
	}

	function cancel() {
		isVisible.value = false;
		resolveFn?.(false);
		resolveFn = null;
	}

	function closeConfirm() {
		isVisible.value = false;
		resolveFn?.(false);
		resolveFn = null;
	}

	return {
		isVisible,
		title,
		subtitle,
		iconClass,
		acceptMessage,
		isDanger,
		openConfirm,
		closeConfirm,
		accept,
		cancel,
	};
}
