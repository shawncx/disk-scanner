package com.msxichen.diskscanner.io;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;


public class UtilitiesTest {
	
	@Test
	public void testWildcardToRegexAsterisk() {
		String wildcard = "C:\\abc*\\*\\abc.jar";
		String wildcard1 = "*.jar";
		String regex = Utilities.wildcardToRegex(wildcard);
		String regex1 = Utilities.wildcardToRegex(wildcard1);
		
		String t1 = "C:\\abc\\zzz\\abc.jar";
		boolean m1 = Pattern.matches(regex, t1);
		Assert.assertTrue(m1);
		
		String t2 = "C:\\abc123\\zzz\\abc.jar";
		boolean m = Pattern.matches(regex, t2);
		Assert.assertTrue(m);
		
		String t3 = "C:\\abc\\zzz\\abc1.jar";
		boolean m3 = Pattern.matches(regex, t3);
		Assert.assertFalse(m3);
		m3 = Pattern.matches(regex1, t3);
		Assert.assertTrue(m3);
	}
	
	@Test
	public void testWildcardToRegexQuestionMark() {
		String wildcard = "C:\\abc?\\abc.jar";
		String regex = Utilities.wildcardToRegex(wildcard);
		
		String t1 = "C:\\abc\\abc.jar";
		boolean m1 = Pattern.matches(regex, t1);
		Assert.assertFalse(m1);
		
		String t2 = "C:\\abcd\\abc.jar";
		boolean m = Pattern.matches(regex, t2);
		Assert.assertTrue(m);
		
		String t3 = "C:\\abc\\abc1.jar";
		boolean m3 = Pattern.matches(regex, t3);
		Assert.assertFalse(m3);
	}
	
	@Test
	public void testWildcardToRegexTilde() {
		String wildcard = "C:\\abc\\a~?bc.jar";
		String regex = Utilities.wildcardToRegex(wildcard);
		
		String t1 = "C:\\abc\\a?bc.jar";
		boolean m1 = Pattern.matches(regex, t1);
		Assert.assertTrue(m1);
		
		String t2 = "C:\\abc\\aabc.jar";
		boolean m = Pattern.matches(regex, t2);
		Assert.assertFalse(m);
	}
	
	@Test
	public void testWildcardToRegexNormal() {
		String wildcard = "C:\\abc\\abc.jar";
		String regex = Utilities.wildcardToRegex(wildcard);
		
		String t1 = "C:\\abc\\abc.jar";
		boolean m1 = Pattern.matches(regex, t1);
		Assert.assertTrue(m1);
		
		String t2 = "C:\\abc\\aabc.jar";
		boolean m = Pattern.matches(regex, t2);
		Assert.assertFalse(m);
	}
	
}
