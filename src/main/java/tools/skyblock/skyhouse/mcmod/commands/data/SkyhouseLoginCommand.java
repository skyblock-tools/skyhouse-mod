package tools.skyblock.skyhouse.mcmod.commands.data;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.util.Constants;

import java.util.Arrays;
import java.util.List;

public class SkyhouseLoginCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "skyhouselogin";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("skyhouselogin", "shlogin");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        ChatComponentText link = new ChatComponentText(EnumChatFormatting.GREEN + Constants.BASE_URL + "/skyhouse/dynaoauth?r=profile%2F"); // INSERT OAUTH LINK
        link.setChatStyle(link.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Constants.BASE_URL + "/skyhouse-v2/dynaoauth?r=profile%2F" + EnumChatFormatting.RESET)));
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "To log in, click the link below and run the command you are given:\n").appendSibling(link));
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
