package vapourdrive.simplestorage.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import vapourdrive.simplestorage.inventory.ContainerExpandableChest;
import vapourdrive.simplestorage.inventory.slots.SlotSearchable;
import vapourdrive.simplestorage.tileentities.TileEntityExpandableChest;

public class GuiExpandableChest extends GuiContainer
{
	ResourceLocation guiTexture = new ResourceLocation("simplestorage", "textures/gui/container/expandablechest.png");
	private final TileEntityExpandableChest tile;
	private final ContainerExpandableChest container;
	private float currentScroll;

	private GuiTextField searchField;
	private boolean wasClicking;
	private boolean isScrolling;
	private boolean isSearching;

	public GuiExpandableChest(InventoryPlayer invPlayer, TileEntityExpandableChest tile)
	{
		super(new ContainerExpandableChest(invPlayer, tile));
		this.tile = tile;
		this.container = ((ContainerExpandableChest) this.inventorySlots);
		this.xSize = 216;
		this.ySize = 215;
		this.allowUserInput = true;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.searchField = new GuiTextField(this.fontRendererObj, this.guiLeft + 105, this.guiTop + 6, 85, this.fontRendererObj.FONT_HEIGHT);
		this.searchField.setMaxStringLength(12);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setVisible(true);
		this.searchField.setTextColor(16777215);

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick)
	{
		super.drawScreen(mouseX, mouseY, partialTick);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		handleSearchInput(mouseX, mouseY, partialTick);
		handleSliderDrag(mouseX, mouseY, partialTick);

		this.updateScreen();
	}
	
	@Override
    protected void mouseClicked(int posX, int posY, int par3)
    {
        super.mouseClicked(posX, posY, par3);
        this.searchField.mouseClicked(posX, posY, par3);
    }
	
	@Override
	protected void handleMouseClick(Slot slot, int par2, int par3, int par4)
	{
		super.handleMouseClick(slot, par2, par3, par4);

		this.isSearching = true;
	}

	public void handleSearchInput(int mouseX, int mouseY, float partialTick)
	{
		if(this.searchField.getText().length() > 0)
		{
			this.hideSlots();
			
			int displaySlot = 0;
			for (int i = 4; i < tile.getSizeInventory(); i++)
			{
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null && displaySlot < 54)
				{
					boolean flag = false;
					String searchbox = this.searchField.getText();
					if (stack.getDisplayName().toLowerCase().contains(searchbox))
					{
						flag = true;
					}
					else if(GameRegistry.findUniqueIdentifierFor(stack.getItem()).modId.toLowerCase().contains(searchbox))
					{
						flag = true;
					}
					else
					{
						int[] ids = OreDictionary.getOreIDs(stack);
						for(int j = 0; j < ids.length; j++)
						{
							if(OreDictionary.getOreName(ids[j]).toLowerCase().contains(searchbox))
							{
								flag = true;
							}
						}
					}
					
					if(flag)
					{
						Slot slot = container.getSlot(i);
						if(slot instanceof SlotSearchable)
						{
							SlotSearchable searchSlot = (SlotSearchable) slot;
							searchSlot.xDisplayPosition = ((displaySlot % 9) * 18) + 31;
							searchSlot.yDisplayPosition = ((displaySlot / 9) * 18) + 18;
						}
						displaySlot++;
					}
				}
			}
		}
		else
		{
			scrollTo(this.currentScroll);
		}
	}

	public void handleSliderDrag(int mouseX, int mouseY, float partialTick)
	{
		boolean flag = Mouse.isButtonDown(0);

		int mouseXmin = this.guiLeft + 197;
		int mouseYmin = this.guiTop + 18;
		int mouseXmax = mouseXmin + 12;
		int mouseymax = mouseYmin + 103;

		if (!this.wasClicking && flag && mouseX >= mouseXmin && mouseY >= mouseYmin && mouseX < mouseXmax && mouseY < mouseymax)
		{
			this.isScrolling = this.isLargeInventory();
		}

		if (!flag)
		{
			this.isScrolling = false;
		}

		this.wasClicking = flag;

		if (this.isScrolling)
		{
			this.currentScroll = ((float) (mouseY - mouseYmin) - 7.5F) / ((float) (mouseymax - mouseYmin) - 15.0F);

			if (this.currentScroll < 0.0F)
			{
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F)
			{
				this.currentScroll = 1.0F;
			}

			scrollTo(this.currentScroll);
		}
	}

	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
		if (Minecraft.getMinecraft().thePlayer != null)
		{
			this.container.onContainerClosed(Minecraft.getMinecraft().thePlayer);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(guiTexture);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (this.isLargeInventory())
		{
			drawTexturedModalRect(guiLeft + 197, guiTop + 18 + (int) (this.currentScroll * 91), 0, 217, 12, 15);
		}
		else
		{
			drawTexturedModalRect(guiLeft + 197, guiTop + 18 + (int) (this.currentScroll * 91), 12, 217, 12, 15);
		}
		searchField.drawTextBox();
	}

	@Override
	protected void keyTyped(char charcter, int keyID)
	{
        if (this.isSearching)
        {
            this.isSearching = false;
            this.searchField.setText("");
        }
        
		if (!this.checkHotbarKeys(keyID))
		{
			if (this.searchField.textboxKeyTyped(charcter, keyID))
			{
				// this.updateSearch(true);
			}
			else
			{
				super.keyTyped(charcter, keyID);
			}
		}
	}

	private boolean isLargeInventory()
	{
		return tile.getAddedRows() > 0;
	}

	public void handleMouseInput()
	{
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.isLargeInventory())
		{

			int j = (tile.getSizeInventory() / 9 - 14) + tile.getAddedRows();

			if (i > 0)
			{
				i = 1;
			}
			if (i < 0)
			{
				i = -1;
			}
			currentScroll -= (double) i / (double) j;
			if (currentScroll < 0.0F)
			{
				currentScroll = 0.0F;
			}
			if (currentScroll > 1.0F)
			{
				currentScroll = 1.0F;
			}
			scrollTo(currentScroll);
		}
		super.handleMouseInput();
	}

	public void scrollTo(float currentScroll)
	{
		this.searchField.setText("");
		this.hideSlots();
		float steps = tile.getAddedRows();
		int newStart = (int) (steps * currentScroll);
		if (newStart < 0)
		{
			newStart = 0;
		}

		if (newStart >= 0)
		{
			for (int i = 0; i < (6 + tile.getAddedRows()); i++)
			{
				for (int j = 0; j < 9; j++)
				{
					int currentNumber = ((i * 9) + j + (newStart * 9)) + 4;

					if (currentNumber < tile.getSizeInventory())
					{
						if (currentNumber >= 0)
						{
							Slot slot = container.getSlot(currentNumber);
							if (slot instanceof SlotSearchable)
							{
								SlotSearchable slotSearchable = (SlotSearchable) container.getSlot(currentNumber);
								if (((i * 9) + j) < 54)
								{
									slotSearchable.xDisplayPosition = (j * 18) + 31;
									slotSearchable.yDisplayPosition = (i * 18) + 18;
								}
								else
								{
									slotSearchable.xDisplayPosition = -1000;
									slotSearchable.yDisplayPosition = -1000;
								}
							}
						}
					}
				}
			}
		}
	}

	public void hideSlots()
	{
		for (int i = 4; i < container.getInventory().size(); i++)
		{
			Slot slot = container.getSlot(i);
			if (slot instanceof SlotSearchable)
			{
				slot.xDisplayPosition = -1000;
				slot.yDisplayPosition = -1000;
			}
		}
	}
}
