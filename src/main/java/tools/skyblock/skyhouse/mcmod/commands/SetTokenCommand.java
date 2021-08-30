package tools.skyblock.skyhouse.mcmod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SetTokenCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "skyhousesettoken";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("shsettoken", "shst");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must specify a token" + EnumChatFormatting.RESET));
            return;
        }
        String token = args[0];
        File file = new File(SkyhouseMod.INSTANCE.getConfigDir(), "very-secret-do-not-share.txt");
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(token);
            writer.close();
            SkyhouseMod.INSTANCE.getAuthenticationManager().loadCredentials();
            if (args.length > 1 && args[1].equals("site")) {
                StringSelection string = new StringSelection("/shst [Redacted]");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(string, string);
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully set token" + EnumChatFormatting.RESET));
        } catch (IOException ignored) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An unknown error occurred" + EnumChatFormatting.RESET));
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Arrays.asList();
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
