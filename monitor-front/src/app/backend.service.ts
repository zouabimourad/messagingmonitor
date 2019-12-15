import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Resolve} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http: HttpClient) {

  }

  getProtocols(): Observable<any> {
    return this.http.get('/api/protocols');
  }

  getAgents(): Observable<any> {
    return this.http.get('/api/agents');
  }

  getBrokers(): Observable<any> {
    return this.http.get('/api/brokers');
  }

  getProducerDefaultCommand(): Observable<any> {
    return this.http.get('/api/producer/defaultCommand');
  }

  getConsumerDefaultCommand(): Observable<any> {
    return this.http.get('/api/consumer/defaultCommand');
  }

  saveBroker(broker: any): Observable<any> {
    return this.http.post('/api/brokers', broker);
  }

  deleteBroker(agentPublicId: string): Observable<any> {
    return this.http.delete(`/api/brokers/${agentPublicId}`);
  }

  getRunningTasks(): Observable<any> {
    return this.http.get('/api/requestRunningTasks');
  }

  cancelRunningTasks(): Observable<any> {
    return this.http.post('/api/cancelRunningTasks' , null);
  }

  postProducerCommand(producerCommand): Observable<any> {
    return this.http.post(`/api/producer`, producerCommand);
  }

  postConsumerCommand(consumerCommand) {
    return this.http.post(`/api/consumer`, consumerCommand);
  }

}

@Injectable()
export class ProtocolsResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getProtocols();
  }
}

@Injectable()
export class AgentsResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getAgents();
  }
}

@Injectable()
export class BrokersResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getBrokers();
  }
}

@Injectable()
export class ProducerDefaultCommandResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getProducerDefaultCommand();
  }
}

@Injectable()
export class ConsumerDefaultCommandResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getConsumerDefaultCommand();
  }
}

@Injectable()
export class RunningTaksResolver implements Resolve<any[]> {
  constructor(private backendService: BackendService) {
  }

  resolve(): Observable<any> {
    return this.backendService.getRunningTasks();
  }
}
