package tools.skyblock.skyhouse.mcmod.commands.data;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;

import java.util.Arrays;
import java.util.List;

public class ReloadConfigCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "reloadskyhouseconfig";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("reloadskyhouseconfig");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        SkyhouseMod.INSTANCE.loadConfig();
        SkyhouseMod.INSTANCE.getAuthenticationManager().loadCredentials();
        DataManager.loadLocalData();
        DataManager.loadStaticSkyhouseData();
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully loaded config"));
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
