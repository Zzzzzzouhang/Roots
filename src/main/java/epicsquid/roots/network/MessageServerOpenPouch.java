/*package epicsquid.roots.network;

import epicsquid.roots.util.PowderInventoryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerOpenPouch implements IMessage {
  public MessageServerOpenPouch() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  public static class MessageHolder implements IMessageHandler<MessageServerOpenPouch, IMessage> {

    @Override
    public IMessage onMessage(MessageServerOpenPouch message, MessageContext ctx) {
      FMLCommonHandler.instance().getInstanceServerInstance().addScheduledTask(() -> handleMessage(message, ctx));

      return null;
    }

    private void handleMessage(MessageServerOpenPouch message, MessageContext ctx) {
      ServerPlayerEntity player = ctx.getServerHandler().player;
      ItemStack pouch = PowderInventoryUtil.getPouch(player);
      if (!pouch.isEmpty()) {
        pouch.getItem().onItemRightClick(player.world, player, Hand.MAIN_HAND);
      }
    }
  }
}*/
