import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerEvent} from "../../../../../model/test/event/runner.event";
import {RunnerEventTypeEnum} from "../../../../../model/test/event/enums/runner-event-type.enum";
import {SuiteStartEvent} from "../../../../../model/test/event/suite-start.event";
import {SuiteEndEvent} from "../../../../../model/test/event/suite-end.event";
import {TestStartEvent} from "../../../../../model/test/event/test-start.event";
import {RunnerTreeNodeTypeEnum} from "../../../../features/tests-runner/tests-runner-tree/model/enums/runner-tree-node-type.enum";
import {TestEndEvent} from "../../../../../model/test/event/test-end.event";
import {StepStartEvent} from "../../../../../model/test/event/step-start.event";
import {StepEndEvent} from "../../../../../model/test/event/step-end.event";
import {RunnerTreeNodeModel} from "../../../../features/tests-runner/tests-runner-tree/model/runner-tree-node.model";
import {RunnerRootTreeNodeModel} from "../../../../features/tests-runner/tests-runner-tree/model/runner-root-tree-node.model";
import {ArrayUtil} from "../../../../../utils/array.util";

export class ResultRunnerTreeUtil {

    static mapRunnerEventsToTreeModel(treeModel: JsonTreeModel, eventsFromServer: Array<RunnerEvent>): JsonTreeModel {


        ResultRunnerTreeUtil.mapRunnerEvents(treeModel, eventsFromServer);

        return treeModel;
    }

}
