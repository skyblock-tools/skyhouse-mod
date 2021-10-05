package tools.skyblock.skyhouse.mcmod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.CraftFlipGui;

import java.util.Arrays;
import java.util.List;

public class CraftFlipCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "craftflip";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("craftflip", "craftflips", "cf");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Congrats, you found an unfinished feature"));
//        SkyhouseMod.INSTANCE.getListener().openGui(new CraftFlipGui());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
