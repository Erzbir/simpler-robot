/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

import love.forte.simbot.BotVerifyInfo;
import love.forte.simbot.BotVerifyInfos;
import love.forte.simbot.Identifies;
import love.forte.simbot.StandardBotVerifyInfoDecoderFactory;
import love.forte.simbot.message.At;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author ForteScarlet
 */
@DisplayName("Java消息序列化")
public class JavaMessageSerializerTest {

    @Test
    public void serializer() {
        final Messages messages = Messages.toMessages(
                new At(Identifies.ID(123)),
                Text.of("Hello "),
                Text.of("World")
        );

        final String jsonString = Messages.toJsonString(messages);

        final Messages messages2 = Messages.fromJsonString(jsonString);

        assert messages.equals(messages2);
        assert messages2.equals(messages);

        final Path path = Paths.get("my-bot.bot.json");
        final BotVerifyInfo info = BotVerifyInfos.toBotVerifyInfo(path, StandardBotVerifyInfoDecoderFactory.json());


    }

}
