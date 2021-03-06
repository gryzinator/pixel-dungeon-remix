package com.watabou.pixeldungeon.items.armor;

import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.actors.hero.HeroSubClass;
import com.watabou.pixeldungeon.utils.GLog;

public class BerserkArmor extends WarriorArmor {
	
	{
		name = Game.getVar(R.string.WarriorArmor_Name);
		image = 6;
		hasHelmet = true;
		coverHair = true;
	}

	@Override
	public boolean doEquip( Hero hero ) {
		if (hero.subClass == HeroSubClass.BERSERKER) {
			return super.doEquip( hero );
		} else {
			GLog.w( TXT_NOT_WARRIOR );
			return false;
		}
	}
}