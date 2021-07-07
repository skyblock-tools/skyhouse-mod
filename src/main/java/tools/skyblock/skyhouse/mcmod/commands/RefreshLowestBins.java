package tools.skyblock.skyhouse.mcmod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

import java.util.Arrays;
import java.util.List;

import static tools.skyblock.skyhouse.mcmod.util.Utils.getLowestBinsFromMoulberryApi;

public class RefreshLowestBins implements ICommand {

    @Override
    public String getCommandName() {
        return "refreshlowestbins";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("refreshlowestbins", "refreshbins");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Refreshing lowest bins..."));
        SkyhouseMod.INSTANCE.getListener().binsManuallyRefreshed = true;
        SkyhouseMod.INSTANCE.getListener().ticksUntilRefreshBins = 0;
        getLowestBinsFromMoulberryApi();
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
