package com.example.minimal.common;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.example.minimal.common.util.StringUtils;

@ControllerAdvice
public class GlobalBindingInitializer {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(String.class, "email", new EmailEditor());
	}

	private static class EmailEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) {
			setValue(StringUtils.normalizeEmail(text));
		}
	}
}
