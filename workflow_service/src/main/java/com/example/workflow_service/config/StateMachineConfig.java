package com.example.workflow_service.config;

import com.example.workflow_service.enums.Patient_Events;
import com.example.workflow_service.enums.Patient_States;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<Patient_States, Patient_Events> {
    @Override
    public void configure(StateMachineStateConfigurer<Patient_States, Patient_Events> states) throws Exception {
        super.configure(states);
        states.withStates()
                .initial(Patient_States.REGISTERED)
                .end(Patient_States.CANCELLED)
                .end(Patient_States.DISCHARGED)
                .states(EnumSet.allOf(Patient_States.class));

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Patient_States, Patient_Events> transitions) throws Exception {
        super.configure(transitions);
        transitions
                .withExternal().source(Patient_States.REGISTERED).target(Patient_States.UNDERTREATMENT).event(Patient_Events.TREATMENT_STARTED)
                .and()
                .withExternal().source(Patient_States.UNDERTREATMENT).target(Patient_States.DISCHARGED).event(Patient_Events.TREATMENT_COMPLETED)
                .and()
                .withExternal().source(Patient_States.REGISTERED).target(Patient_States.DISCHARGED).event(Patient_Events.SKIP_TREATMENT)
                .and()
                .withExternal().source(Patient_States.REGISTERED).target(Patient_States.CANCELLED).event(Patient_Events.APPOINTMENT_CANCELLED)
                .and()
                .withExternal().source(Patient_States.UNDERTREATMENT).target(Patient_States.CANCELLED).event(Patient_Events.APPOINTMENT_CANCELLED);
    }
}
