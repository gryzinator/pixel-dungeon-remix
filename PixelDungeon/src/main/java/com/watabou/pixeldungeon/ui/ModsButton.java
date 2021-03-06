package com.watabou.pixeldungeon.ui;

import android.Manifest;

import com.nyrds.android.util.DownloadStateListener;
import com.nyrds.android.util.DownloadTask;
import com.nyrds.android.util.FileSystem;
import com.nyrds.android.util.GuiProperties;
import com.nyrds.android.util.Mods;
import com.nyrds.android.util.Util;
import com.nyrds.pixeldungeon.windows.DownloadProgressWindow;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.InterstitialPoint;
import com.watabou.noosa.SystemText;
import com.watabou.noosa.Text;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.windows.WndMessage;
import com.watabou.pixeldungeon.windows.WndModSelect;
import com.watabou.pixeldungeon.windows.WndTitledMessage;

import java.io.File;

public class ModsButton extends Button implements InterstitialPoint, DownloadStateListener.IDownloadComplete {

	private Image image;
	private Text  text;

	private WndMessage downloadProgress;

	static private boolean needUpdate;

	public ModsButton() {
		super();

		width = image.width;
		height = image.height;
	}

	static public void modUpdated() {
		needUpdate = true;
	}

	@Override
	public void update() {
		if(needUpdate) {
			needUpdate = false;
			text.text(PixelDungeon.activeMod());
		}
		super.update();
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		image = Icons.MODDING_MODE.get();
		add(image);

		text = new SystemText(GuiProperties.regularFontSize());
		text.text(PixelDungeon.activeMod());
		add(text);
	}

	@Override
	protected void layout() {
		super.layout();

		image.x = x;
		image.y = y;

		text.x = x;
		text.y = image.y + image.height + 2;
	}

	@Override
	protected void onTouchDown() {
		image.brightness(1.5f);
		Sample.INSTANCE.play(Assets.SND_CLICK);
	}

	@Override
	protected void onTouchUp() {
		image.resetColor();
	}

	@Override
	protected void onClick() {
		String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
		Game.instance().doPermissionsRequest(this, requiredPermissions);
	}

	@Override
	public void returnToWork(final boolean result) {
		final Group parent = getParent();
		Game.pushUiTask(new Runnable() {
			@Override
			public void run() {
				if (result) {
					if(Util.isConnectedToInternet()) {
						File modsCommon = FileSystem.getExternalStorageFile(Mods.MODS_COMMON_JSON);
						modsCommon.delete();
						String downloadTo = modsCommon.getAbsolutePath();

                        new DownloadTask(new DownloadProgressWindow("Downloading",ModsButton.this)).download("https://raw.githubusercontent.com/NYRDS/pixel-dungeon-remix-mods/master/mods.json", downloadTo);

					} else {
						DownloadComplete("no internet", true);
					}

				} else {
					parent.add(new WndTitledMessage(Icons.get(Icons.SKULL), "No permissions granted", "No permissions granted"));
				}
			}
		});

	}

	@Override
	public void DownloadComplete(String file, final Boolean result) {
		Game.pushUiTask(new Runnable() {
			@Override
			public void run() {
				Game.scene().add(new WndModSelect());

				if (!result) {
					Game.toast("Mod list download failed :(");
				}
			}
		});
	}
}
