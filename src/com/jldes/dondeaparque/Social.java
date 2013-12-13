package com.jldes.dondeaparque;

import android.content.Context;
import android.content.Intent;

public class Social {
	/**
	 * Open a contextual Menu with the available applications to share
	 *
	 * @param the Context (to open the menú and the new activity)
	 * @param the subject
	 * @param the text
	 */
	public static void share(Context ctx, String subject,String text) {
		 final Intent intent = new Intent(Intent.ACTION_SEND);

		 intent.setType("text/plain");
		 intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		 intent.putExtra(Intent.EXTRA_TEXT, text);

		 ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.app_name)));
		}
}
