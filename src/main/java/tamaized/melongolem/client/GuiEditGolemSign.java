package tamaized.melongolem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;
import tamaized.melongolem.MelonMod;
import tamaized.melongolem.common.EntityMelonGolem;
import tamaized.melongolem.network.server.ServerPacketHandlerMelonSign;

public class GuiEditGolemSign extends GuiScreen {

	private final EntityMelonGolem golem;
	private int updateCounter;
	private int editLine;
	private GuiButton doneBtn;
	private boolean canSend = true;

	public GuiEditGolemSign(EntityMelonGolem golem) {
		this.golem = golem;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.doneBtn = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		if (canSend)
			MelonMod.network.sendToServer(new ServerPacketHandlerMelonSign.Packet(golem));
	}

	@Override
	public void updateScreen() {
		++this.updateCounter;
		if (golem.getDistanceToEntity(Minecraft.getMinecraft().player) > 6) {
			canSend = false;
			Minecraft.getMinecraft().player.closeScreen();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 0) {
				this.mc.displayGuiScreen(null);
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 200) {
			this.editLine = this.editLine - 1 & 3;
		}

		if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
			this.editLine = this.editLine + 1 & 3;
		}

		String s = golem.getSignText(editLine).getUnformattedText();

		if (keyCode == 14 && !s.isEmpty()) {
			s = s.substring(0, s.length() - 1);
		}

		if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRenderer.getStringWidth(s + typedChar) <= 90) {
			s = s + typedChar;
		}

		golem.setSignText(editLine, new TextComponentString(s));

		if (keyCode == 1) {
			this.actionPerformed(this.doneBtn);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (this.width / 2), 0.0F, 50.0F);
		float f = 93.75F;
		GlStateManager.scale(-93.75F, -93.75F, -93.75F);
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		/*Block block = golem.getBlockType();

		if (block == Blocks.STANDING_SIGN) {
			float f1 = (float) (this.tileSign.getBlockMetadata() * 360) / 16.0F;
			GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0625F, 0.0F);
		} else {
			int i = this.tileSign.getBlockMetadata();
			float f2 = 0.0F;

			if (i == 2) {
				f2 = 180.0F;
			}

			if (i == 4) {
				f2 = 90.0F;
			}

			if (i == 5) {
				f2 = -90.0F;
			}

			GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
		}*/
		GlStateManager.translate(0.0F, -1.0625F, 0.0F);

		if (this.updateCounter / 6 % 2 == 0) {
			EntityMelonGolem.te.lineBeingEdited = this.editLine;
		}

		for (int index = 0; index < 4; index++)
			EntityMelonGolem.te.signText[index] = golem.getSignText(index);
		TileEntityRendererDispatcher.instance.render(EntityMelonGolem.te, -0.5D, -0.75D, -0.5D, 0.0F);
		EntityMelonGolem.te.lineBeingEdited = -1;
		GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}


}
