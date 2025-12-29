import GlobalIndicators from './GlobalIndicators';
import AlertsWidget from './AlertsWidget';
import ActivityTimeline from './ActivityTimeline';

const Overview = () => {
    return (
        <div className="space-y-6">
            <GlobalIndicators />
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 h-96">
                <AlertsWidget />
                <ActivityTimeline />
            </div>
        </div>
    );
};

export default Overview;
