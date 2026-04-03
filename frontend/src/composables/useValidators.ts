import type {ValidatorFn} from "./useFormField.ts";

export function useValidators() {
	function required(value: any): ReturnType<ValidatorFn<string>> {
		if (value === null || value === undefined || value === "") {
			return "This field is required";
		}
		return null;
	}

	function email(value: string | null): ReturnType<ValidatorFn<string>> {
		const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		if (!pattern.test(String(value))) {
			return "Email address is not in a valid format";
		}
		return null;
	}

	function minLength(min: number): ValidatorFn<string> {
		return function (value: string | null): string | null {
			if (!value) return null;
			if (value.length < min) {
				return `Field must contain at least ${min} characters`;
			}
			return null;
		};
	}

	function maxLength(max: number): ValidatorFn<string> {
		return function (value: string | null): string | null {
			if (!value) return null;
			if (value.length > max) {
				return `Field must not contain more than ${max} characters`;
			}
			return null;
		};
	}

	function minValue(min: number): ValidatorFn<number> {
		return function (value: number | null): string | null {
			if (value === null) return null;
			if (value < min) {
				return `Value must not be less than ${min}`;
			}
			return null;
		};
	}

	function maxValue(max: number): ValidatorFn<number> {
		return function (value: number | null): string | null {
			if (value === null) return null;
			if (value > max) {
				return `Value must not be greater than ${max}`;
			}
			return null;
		};
	}

	return {
		required,
		email,
		minLength,
		maxLength,
		minValue,
		maxValue,
	};
}
