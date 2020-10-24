package extracells.container

import appeng.api.AEApi
import appeng.api.implementations.guiobjects.IGuiItem
import appeng.api.implementations.guiobjects.INetworkTool
import extracells.container.slot.SlotNetworkTool
import extracells.container.slot.SlotRespective
import extracells.gui.GuiBusFluidStorage
import extracells.part.PartFluidStorage
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

class ContainerBusFluidStorage(part: PartFluidStorage, player: EntityPlayer) : Container() {
    private var guiBusFluidStorage: GuiBusFluidStorage? = null
    var part: PartFluidStorage
    protected fun bindPlayerInventory(inventoryPlayer: IInventory?) {
        for (i in 0..2) {
            for (j in 0..8) {
                addSlotToContainer(Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, i * 18 + 140))
            }
        }
        for (i in 0..8) {
            addSlotToContainer(Slot(inventoryPlayer, i, 8 + i * 18, 198))
        }
    }

    override fun canInteractWith(entityplayer: EntityPlayer): Boolean {
        return part.isValid
    }

    fun setGui(_guiBusFluidStorage: GuiBusFluidStorage?) {
        guiBusFluidStorage = _guiBusFluidStorage
    }

    override fun transferStackInSlot(player: EntityPlayer, slotnumber: Int): ItemStack {
        if (guiBusFluidStorage != null) guiBusFluidStorage!!.shiftClick(getSlot(slotnumber).stack)
        var itemstack: ItemStack? = null
        val slot = inventorySlots[slotnumber] as Slot?
        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()
            if (slotnumber < 36) {
                if (!mergeItemStack(itemstack1, 36, inventorySlots.size, true)) {
                    return null
                }
            } else if (!mergeItemStack(itemstack1, 0, 36, false)) {
                return null
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null)
            } else {
                slot.onSlotChanged()
            }
        }
        return itemstack!!
    }

    init {
        addSlotToContainer(SlotRespective(part.upgradeInventory, 0, 187, 8))
        this.part = part
        bindPlayerInventory(player.inventory)
        for (i in 0 until player.inventory.sizeInventory) {
            val stack = player.inventory.getStackInSlot(i)
            if (stack != null && AEApi.instance().definitions().items().networkTool().isSameAs(stack)) {
                val coord = part.host.location
                val guiItem = stack.item as IGuiItem
                val networkTool = guiItem.getGuiObject(stack, coord.world, coord.x, coord.y, coord.z) as INetworkTool
                for (j in 0..2) {
                    for (k in 0..2) {
                        addSlotToContainer(SlotNetworkTool(networkTool, j + k * 3, 187 + k * 18, j * 18 + 102))
                    }
                }
                return
            }
        }
    }
}