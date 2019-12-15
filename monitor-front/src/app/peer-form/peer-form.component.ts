import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';

@Component({
  selector: 'app-peer-form',
  templateUrl: './peer-form.component.html',
  styleUrls: ['./peer-form.component.css']
})
export class PeerFormComponent implements OnInit {

  @Input()
  config: any;

  @Input()
  agents: any;

  @Input()
  brokers: any;

  @Input()
  protocols: any;

  @Input()
  loading: boolean;

  @Input()
  parentForm: FormGroup;

  @Output()
  submit: EventEmitter<any> = new EventEmitter();

  constructor() {

  }

  get protocolBrokers(): any {

    return this.brokers.filter(broker => {
      const protocolValue = this.parentForm.controls.protocol.value;
      if (protocolValue === 'MQTT' && broker.supportsMqtt) {
        return true;
      } else if (protocolValue === 'AMQP' && broker.supportsAmqp) {
        return true;
      } else if (broker.publicId === 'EMBEDDED') {
        return true;
      } else {
        return false;
      }
    });

  }

  ngOnInit() {

    const controls = this.parentForm.controls;

  }

  onProtocolChange(code: any) {
    const brokerPublicId = this.parentForm.controls.brokerPublicId;
    const broker = this.brokers.find(it => it.publicId === brokerPublicId.value);
    if (code === 'MQTT' && !broker.supportsMqtt || code === 'AMQP' && !broker.supportsAmqp) {
      brokerPublicId.setValue('EMBEDDED');
    }
  }

  command() {
    this.submit.next();
  }
}
