import {computed} from "vue";
import type {useFormField} from "./useFormField";

export function useForm<T extends Record<string, ReturnType<typeof useFormField<any>>>>(formFields: T) {

	function validateForm(): boolean {
		let allFieldsAreValid: boolean = true;
		const fieldNames = Object.keys(formFields) as Array<keyof T>;

		for (const fieldName of fieldNames) {
			const currentField = formFields[fieldName];

			const isCurrentFieldValid = currentField?.validate();
			currentField?.markAsTouched();

			if (!isCurrentFieldValid) {
				allFieldsAreValid = false;
			}
		}

		return allFieldsAreValid;
	}

	function resetForm(): void {
		const fieldNames = Object.keys(formFields) as Array<keyof T>;

		for (const fieldName of fieldNames) {

			const currentField = formFields[fieldName];
			currentField?.resetFormField();
		}
	}

	const formValue = computed<Record<string, any>>(() => {
		const currentValues: Record<string, any> = {};
		const fieldNames = Object.keys(formFields) as Array<keyof T>;

		for (const fieldName of fieldNames) {
			const currentField = formFields[fieldName];
			currentValues[fieldName as string] = currentField?.getInputValue();
		}

		return currentValues;
	});

	function toJson<R extends Record<string, any>>(): R {
		return JSON.parse(JSON.stringify(formValue.value)) as R;
	}

	return {
		...formFields,
		validateForm,
		resetForm,
		toJson,
	};
}
