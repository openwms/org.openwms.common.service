/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.transport.commands;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.transport.api.commands.Command;
import org.openwms.common.transport.api.commands.MessageCommand;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * A TransportUnitCommandListener is listening on {@link TUCommand}s to process.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 * @see TUCommand
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@TxService
class TransportUnitCommandListener {

    private final TransportUnitCommandHandler handler;
    private final MessageCommandHandler messageCommandHandler;

    TransportUnitCommandListener(TransportUnitCommandHandler handler, MessageCommandHandler messageCommandHandler) {
        this.handler = handler;
        this.messageCommandHandler = messageCommandHandler;
    }

    @Measured
    @RabbitListener(queues = "${owms.commands.common.tu.queue-name}")
    public void onCommand(@Payload Command command) {
        try {
            if (command instanceof TUCommand) {
                handler.handle((TUCommand) command);
            } else if (command instanceof MessageCommand) {
                messageCommandHandler.handle((MessageCommand) command);
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
