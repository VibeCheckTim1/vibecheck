import {computed, ref} from "vue";

export type ValidatorFn<T> = (value: T | null) => string | null;
type InputType = string | number | boolean | Date | null;

export function useFormField<T extends string | number | boolean | Date>(
	initialValue: InputType = null,
	validators: ValidatorFn<T>[] = []
) {
	const rawValue = ref<InputType>(initialValue);
	const errors = ref<string[]>([]);
	const touched = ref(false);

	function convertValue(value: InputType): T | null {
		if (value == null || value === "") {
			return null as T | null;
		}

		if (typeof value === "boolean") {
			return value as T;
		}

		if (typeof value === "string") {
			if ((Number(value).toString() === value) && typeof ({} as T) === "number") {
				return Number(value) as T;
			}

			if ((value === "true" || value === "false") && typeof ({} as T) === "boolean") {
				return (value === "true") as T;
			}

			return value as T;
		}

		if (typeof value === "number") {
			return value as T;
		}

		return value as T;
	}

	const fieldValue = computed<T | null>(() => convertValue(rawValue.value));

	function getInputValue(): T {
		return fieldValue.value as T;
	}

	function setInputValue(value: InputType | undefined): void {
		rawValue.value = value ?? null;
		markAsTouched();
		validate();
	}

	function validate(): boolean {
		const validationErrors: string[] = [];
		for (const validator of validators) {
			const result = validator(fieldValue.value);
			if (result) validationErrors.push(result);
		}
		errors.value = validationErrors;
		return validationErrors.length === 0;
	}

	function markAsTouched() {
		touched.value = true;
	}

	function resetFormField(): void {
		rawValue.value = initialValue;
		errors.value = [];
		touched.value = false;
	}

	return {
		setInputValue,
		getInputValue,
		errors,
		touched,
		validate,
		markAsTouched,
		resetFormField,
		fieldValue,
	};
}

